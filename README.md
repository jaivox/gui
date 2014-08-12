Jaivox Application Generator (JAG) version 0.2 March 2014
Modified August 2014 to utilize Google's web recognizer API key

Copyright 2010-2014 by Bits and Pixels, Inc.
Developed by Rajesh John rajesh@jaivox.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License in file LICENSE or at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Please see work/licenses for licenses to other components included with
this package.
================================
Please see http://www.chromium.org/developers/how-tos/api-keys
Please read instructions there, you need to join the Chromium developer's group.
Add this key as the value of "googleapikey" in your conf file

The executable is the jar file jag.jar, you run this using java.

java -jar dist/jag.jar

Please see 

	http://jaivox.com/guitool.html
	
for tutorial and other details.

To build the program in a directory called jagbuild, run

ant -buildfile jagbuild.xml

from this directory. This will create a distributable jag.zip and a runnable
jag.jar.

(March 17, 2013)
