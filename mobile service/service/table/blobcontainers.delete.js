var azure = require('azure');
 
function del(id, user, request) {
    
    var accountName = 'nooz';
    var accountKey = 'sSa0gMslEzHUQ6ffxbf6Np5q2vCz02HhMirf7yLXOEqykz75zyxFyOUoJNIbhRS+5J9uR8RPokc/6PEUescGVg==';
    var host = accountName + '.blob.core.windows.net';
    var blobService = azure.createBlobService(accountName, accountKey, host);
    
    blobService.deleteContainer(request.parameters.containerName, function (error) {
        if (!error) {
            request.respond(200);
        } else {
            request.respond(500);
        }
    });
}