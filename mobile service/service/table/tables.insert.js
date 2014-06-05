var azure = require('azure');
 
function insert(item, user, request) {
    
    var accountName = 'nooz';
    var accountKey = 'sSa0gMslEzHUQ6ffxbf6Np5q2vCz02HhMirf7yLXOEqykz75zyxFyOUoJNIbhRS+5J9uR8RPokc/6PEUescGVg==';
    var host = accountName + '.table.core.windows.net';
    var tableService = azure.createTableService(accountName, accountKey, host);
    
    tableService.createTable(item.tableName, function (error) {
        if (!error) {
            request.respond(200, item);
        } else {
            request.respond(500);
        }
    });
}