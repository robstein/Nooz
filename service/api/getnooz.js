exports.post = function(request, response) {
    // Use "request.service" to access features of your mobile service, e.g.:
    //   var tables = request.service.tables;
    //   var push = request.service.push;
    var mssql = request.service.mssql;                       
    var sql = "SELECT A.firstName as firstName, " +
              "A.lastName as lastName, " +
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
              "ON A.id = S.author_id " +
              "WHERE S.lat > ? AND s.lat < ? AND s.lng > ? AND s.lng < ? "
              " ";
        mssql.query(sql, [request.body.user_id, request.body.southwestLat, request.body.northeastLat, request.body.southwestLng, request.body.northeastLng], {
            success: function(results) {            
                response.send(200, results);
            }
        })
};

exports.get = function(request, response) {
    response.send(statusCodes.OK, { message : 'Hello World!' });
};