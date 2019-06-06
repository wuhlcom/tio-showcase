call mvn clean install

call rd ..\..\..\..\dist\examples\im\client /s /q
call xcopy target\dist\tio-examples-im-client-3.1.0.v20180705-RELEASE ..\..\..\..\dist\examples\im\client\ /s /e /q /y

