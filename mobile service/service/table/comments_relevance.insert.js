function insert(item, user, request) {

	var commentRelevanceTable = tables.getTable('comments_relevance');
    commentRelevanceTable.where({ user_id : item.user_id, story_id : item.story_id }).read({
			success: function(results) {
				if (results.length === 1) {
                    // We already have a record, we need to update it
                    results[0].input = item.input;
                    commentRelevanceTable.update(results[0]);
                    request.respond(200, results[0]);
				} else {
                    // We don't have a record, we need to create it
					request.execute();
				}
			}
		});

}