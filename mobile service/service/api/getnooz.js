exports.post = function(request, response) {
  var mssql = request.service.mssql;
  var sql = "SELECT A.name as authorName, " +
            "A.id as authorId, " +
            "S.id as id, " +
            "S.medium as medium, " +
            "S.category as category, " +
            "S.headline as headline, " +
            "S.caption as caption, " +
            "S.keyword1 as keyword1, " +
            "S.keyword2 as keyword2, " +
            "S.keyword3 as keyword3, " +
            "S.lat as lat, " +
            "S.lng as lng, " +
            "S.__createdAt as __createdAt, " +
            "(select input from relevance where user_id = ? and story_id = S.id) as user_relevance, " +
            "(select count(input) from relevance where input > 0 and story_id = S.id) as relevantScore, " +
            "(select count(input) from relevance where input < 0 and story_id = S.id) as irrelevantScore " +
            "FROM stories as S " +
            "INNER JOIN accounts as A " +
            "ON A.id = S.author_id ";
  var queryParams;
  if ("PROFILE" == request.body.searchType) {
    sql = sql + "WHERE A.id = ? ";
    queryParams = [request.body.user_id, request.body.author_user_id];
  } else {
    sql = sql + "WHERE S.lat > ? AND s.lat < ? AND s.lng > ? AND s.lng < ? ";
    if (false == request.body.defaultMedium) {
    if ((request.body.audio) && (request.body.picture)) {
        sql = sql + "and (S.medium = 'AUDIO' or S.medium = 'PICTURE') ";
      } else if ((request.body.audio) && (request.body.video)) {
        sql = sql + "and (S.medium = 'AUDIO' or S.medium = 'VIDEO') ";
      } else if ((request.body.picture) && (request.body.video)) {
        sql = sql + "and (S.medium = 'PICTURE' or S.medium = 'VIDEO') ";
      } else if (request.body.audio) {
        sql = sql + "and S.medium = 'AUDIO' ";
      } else if (request.body.picture) {
        sql = sql + "and S.medium = 'PICTURE' ";
      } else if (request.body.video) {
        sql = sql + "and S.medium = 'VIDEO' ";   
      }
    }
    if (false == request.body.defaultCategory) {
      sql = sql + "and (";
      var first = true;
      if (request.body.people) {
        sql = sql + "S.category = 'People' ";
        first = false;
      } 
      if (request.body.community) {
        if(first) {
          sql = sql + "S.category = 'Community' ";
        } else {
          sql = sql + "or S.category = 'Community' ";
        }
        first = false;
      }
      if (request.body.sports) {
        if(first) {
          sql = sql + "S.category = 'Sports' ";
        } else {
          sql = sql + "or S.category = 'Sports' ";
        }
        first = false;
      }
      if (request.body.food) {
        if(first) {
          sql = sql + "S.category = 'Food' ";
        } else {
          sql = sql + "or S.category = 'Food' ";
        }
        first = false;
      }
      if (request.body.publicSafety) {
        if(first) {
          sql = sql + "S.category = 'Public Safety' ";
        } else {
          sql = sql + "or S.category = 'Public Safety' ";
        }
        first = false;
      }
      if (request.body.artsAndLife) {
        if(first) {
          sql = sql + "S.category = 'Arts & Life' ";
        } else {
          sql = sql + "or S.category = 'Arts & Life' ";
        }
        first = false;
      } 
      sql = sql + ") ";
    }

    queryParams = [request.body.user_id, request.body.southwestLat, request.body.northeastLat, request.body.southwestLng, request.body.northeastLng];
  }

  console.log(sql);
  console.log(request.body);

  mssql.query(sql, queryParams, {
    success: function(results) {
      if ("RELEVANT" == request.body.searchType) {
        // RELEVANT ALGO
        // Reverse to get newest to oldest
        // Rank by rankingFunction compare logic
        // Slice the array, take the first 10 elements (if there are that many)
        response.send(200, results.reverse().sort(rankingFunction).slice(0, Math.min(results.length, 1000)));
      }
      if ("BREAKING" == request.body.searchType) {
        // BREAKING ALGO
        // Reverse to get newest to oldest
        // Slice the array, take the first 10 elements (if there are that many)              
        response.send(200, results.reverse().slice(0, Math.min(results.length, 1000)));
      }
      if  ("PROFILE" == request.body.searchType) {
        // PROFILE stories
        // Reverse to get newest to oldest
        response.send(200, results.reverse());
      }
    }
  })

};

function rankingFunction(a,b) {
  if (a.relevantScore - a.irrelevantScore < b.relevantScore - b.irrelevantScore)
    return 1;
  if (a.relevantScore - a.irrelevantScore > b.relevantScore - b.irrelevantScore)
    return -1;
  return 0;
}

exports.get = function(request, response) {
    response.send(statusCodes.OK, { message : 'Hello World!'});
};