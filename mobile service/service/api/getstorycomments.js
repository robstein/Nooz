exports.post = function(request, response) {
    var rankingAlgos = require('./shared').rankingAlgos;
    var mssql = request.service.mssql;
    /*
    SELECT StoryComment.id AS id,
           StoryComment.parent_id AS parentId,
           StoryComment.text AS text,
           StoryComment.__createdAt AS createdAt,
    
      (SELECT INPUT
       FROM comments_relevance
       WHERE voter_id = ?
         AND comment_id = StoryComment.id) AS currentUserVote,
    
      (SELECT count(INPUT)
       FROM comments_relevance
       WHERE INPUT > 0
         AND comment_id = StoryComment.id) AS up,
    
      (SELECT count(INPUT)
       FROM comments_relevance
       WHERE INPUT < 0
         AND comment_id = StoryComment.id) AS down,
    
      (SELECT name
       FROM accounts
       WHERE id = StoryComment.commenter_id) AS commenterName
    FROM Comments AS StoryComment
    WHERE StoryComment.story_id = ?
    */
    var sql = "SELECT StoryComment.id AS id, StoryComment.parent_id AS parentId, StoryComment.text AS text, StoryComment.__createdAt AS createdAt, (SELECT INPUT FROM comments_relevance WHERE voter_id = ? AND comment_id = StoryComment.id) AS currentUserVote, (SELECT count(INPUT) FROM comments_relevance WHERE INPUT > 0 AND comment_id = StoryComment.id) AS up, (SELECT count(INPUT) FROM comments_relevance WHERE INPUT < 0 AND comment_id = StoryComment.id) AS down, (SELECT name FROM accounts WHERE id = StoryComment.commenter_id) AS commenterName FROM Comments AS StoryComment WHERE StoryComment.story_id = ?";
    var queryParams = [request.body.user_id, request.body.story_id];
    
    console.log(sql + "\n\n" + request.body);
    
    mssql.query(sql, queryParams, {
        success: function(results) {
            // RELEVANT ALGO
            // Reverse to get newest to oldest
            // Rank with algo
            response.send(200, results.reverse().sort(rankingAlgos["RelevanceMinusIrrelevance"]));
        }
    })

};

exports.get = function(request, response) {
    response.send(statusCodes.OK, { message : 'Hello World!' });
};