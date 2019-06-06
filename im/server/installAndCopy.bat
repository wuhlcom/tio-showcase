call mvn clean install

call rd ..\..\..\..\dist\examples\im\server /s /q
call xcopy target\dist\tio-examples-im-server-3.1.0.v20180705-RELEASE ..\..\..\..\dist\examples\im\server\ /s /e /q /y

