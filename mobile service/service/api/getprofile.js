exports.post = function(request, response) {
    var mssql = request.service.mssql;
    var sql = "SELECT id AS id, " +
                     "firstName AS firstName, " +
                     "lastName AS lastName, " +
                     "user_location AS homeLocation, " +
                     "firstName AS firstName, " +
              "" +
                "(SELECT count(R.input) " +
                "FROM relevance AS R " +
                 "INNER JOIN stories AS S ON R.story_id = S.id " +
                 "WHERE S.author_id = ? " +
                   "AND R.input > 0) AS userScore " +
              "FROM accounts " +
              "WHERE id = ?";
              

        console.log(request.body);
        console.log(sql);
        mssql.query(sql, [request.body.user_id, request.body.user_id], {
            success: function(results) {
                if (results.length > 0) {
                    response.send(200, results[0]);
                }
            }
        })
};

exports.get = function(request, response) {
    response.send(statusCodes.OK, { message : 'Hello World!' });
};