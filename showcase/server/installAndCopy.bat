call mvn clean install

call rd ..\..\..\..\dist\examples\showcase\server /s /q
call xcopy target\dist\tio-examples-showcase-server-2.0.0.v20170824-RELEASE ..\..\..\..\dist\examples\showcase\server\ /s /e /q /y

