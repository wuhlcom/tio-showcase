call mvn clean install

call rd ..\..\..\..\dist\examples\helloworld\server /s /q
call xcopy target\dist\tio-examples-helloworld-server-2.0.0.v20170824-RELEASE ..\..\..\..\dist\examples\helloworld\server\ /s /e /q /y

