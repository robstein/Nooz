var azure = require('azure');
 
function read(query, user, request) {
    
    var accountName = 'nooz';
    var accountKey = 'sSa0gMslEzHUQ6ffxbf6Np5q2vCz02HhMirf7yLXOEqykz75zyxFyOUoJNIbhRS+5J9uR8RPokc/6PEUescGVg==';
    var host = accountName + '.blob.core.windows.net';
    var blobService = azure.createBlobService(accountName, accountKey, host);
    
    
    blobService.listBlobs(request.parameters.container, function (error, blobs) {
        if (error) {
            request.respond(500, error);
        } else {
            request.respond(200, blobs)
        }
    });
}