function account_names_fix() {
    var accountsTable = tables.getTable('accounts');
    accountsTable.read({
    	success: function(results) {
            for (var i = 0; i < results.length; ++i) {
                results[i].name = results[i].firstName + " " + results[i].lastName;
                console.log(results[i]);
                accountsTable.update(results[i]);
            }
    	}
    });

}