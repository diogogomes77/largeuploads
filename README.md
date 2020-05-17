# largeuploads
Spring Boot very large uploads

## To run 
`docker-compose up`
The initial build takes some minutes depending on the Internet connection.
Wait until the terminal stops scrolling.

## To test
Open a web-browser on `http://127.0.0.1`

There’s a simple form to upload a file.
Any uploaded files will be listed on the web-page, underneath the form.
The files are saved on the folder `upload_folder` in the root directory. This folder is mapped into the spring_run container as a volume on docker-compose.
For the purpose of testing a large file upload, my suggestion is to replace the actual `upload_folder` using a separate partition or external drive as the `upload_folder` by the means of  a symbolic link.
As such:
`ln -s /dev/sdb/some_folder ./upload_folder`
Then, on the terminal, you can run a `watch df -h` during the large file upload and watch the used disk space staled while the destination partition will increase. 

Another way to upload files is using a cURL command on the endpoint http://127.0.0.1/upload, as such:
`curl -X POST -F file=@/home/dgomes/Downloads/WHDLOAD.7z http://127.0.0.1/upload`
