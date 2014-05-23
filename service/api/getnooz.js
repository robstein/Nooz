exports.post = function(request, response) {
    // Use "request.service" to access features of your mobile service, e.g.:
    //   var tables = request.service.tables;
    //   var push = request.service.push;
    var mssql = request.service.mssql;                       
    var sql = "SELECT A.firstName as firstName, " +
              "A.lastName as lastName, " +
              "S.id as id, " +
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
              " ";
        mssql.query(sql, [request.body.user_id], {
            success: function(results) {
                console.log(results); 
                var i;
                for (i = 0; i < results.length; ++i) {
                    if (outOfBounds(results[i].lat, results[i].lng, request.body.mapCorners)) {
                        results.splice(i--, 1);
                    }
                }     
                response.send(200, results);
            }
        })
};

exports.get = function(request, response) {
    response.send(statusCodes.OK, { message : 'Hello World!' });
};

function outOfBounds(lat, lng, mapCorners) {
    var ab = distance(mapCorners.topLeft.lat, mapCorners.topLeft.lng, mapCorners.topRight.lat, mapCorners.topRight.lng);
    var ad = distance(mapCorners.topLeft.lat, mapCorners.topLeft.lng, mapCorners.bottomLeft.lat, mapCorners.bottomLeft.lng);
    var pa = distance(lat, lng, mapCorners.topLeft.lat, mapCorners.topLeft.lng);
    var pb = distance(lat, lng, mapCorners.topRight.lat, mapCorners.topRight.lng);
    var pc = distance(lat, lng, mapCorners.bottomRight.lat, mapCorners.bottomRight.lng);
    var pd = distance(lat, lng, mapCorners.bottomLeft.lat, mapCorners.bottomLeft.lng);    
        
    var a_apd = pa*pd/2;
    var a_dpc = pd*pc/2;
    var a_cpb = pc*pb/2;
    var a_bpa = pb*pa/2;
    
    if (a_apd + a_dpc + a_cpb + a_bpa == ab * ad) {
        return true;
    } else {
        return false;
    }
}

function distance(lat1, lng1, lat2, lng2) {
    var lat_diff = lat2 - lat1;
    var lng_diff = lng2 - lng1;
    return Math.sqrt(lat_diff*lat_diff+lng_diff*lng_diff);
}