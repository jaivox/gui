/*
   Jaivox Application Generator (JAG) version 0.2 March 2014
   Copyright 2010-2014 by Bits and Pixels, Inc.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

Please see work/licenses for licenses to other components included with
this package.
*/

package com.jaivox.ui.gengram;

public class WnLinkBasic implements WnLink {

	@Override
	public String[] getsynonyms (String word) {
		return null;
	}

	@Override
	public String[] synsget (String word, String form) {
		return null;
	}

	@Override
	public void synsput (String word, String[] words, String form) {
	}

	@Override
	public void addtablecolumn (String filename, String sep, int columns, int column) {
	}

	@Override
	public void dumpSynonyms () {
	}

	@Override
	public void createsyns () {
	}

	@Override
	public void synsremove (String word, String syn, String form) {
	}
}
