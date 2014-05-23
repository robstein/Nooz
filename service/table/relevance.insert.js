function insert(item, user, request) {

	var relevanceTable = tables.getTable('relevance');
    relevanceTable.where({ user_id : item.user_id, story_id : item.story_id }).read({
			success: function(results) {
				if (results.length === 1) {
                    // We already have a record, we need to update it
                    results[0].input = item.input;
                    relevanceTable.update(results[0]);
                    console.log('Updated relevance');
                    request.respond(200, results[0]);
				} else {
                    // We don't have a record, we need to create it
                    console.log('Added relevance');
					request.execute();
				}
			}
		});

}