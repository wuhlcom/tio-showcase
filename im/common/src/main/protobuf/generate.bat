rd .\src\ /s/q

md .\src\c
md .\src\java
md .\src\python
md .\src\js
md .\src\commonjs

protoc.exe --cpp_out=.\src\c chat.proto

protoc.exe --java_out=.\src\java chat.proto

protoc.exe --python_out=.\src\python chat.proto

rem protoc.exe --js_out=.\src\js chat.proto

protoc.exe --js_out=library=im_proto,binary:.\src\js chat.proto

protoc.exe --js_out=import_style=commonjs,binary:.\src\commonjs chat.proto

pause