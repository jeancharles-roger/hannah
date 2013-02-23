Hannah
======

Hannah is a library that proposes a simple API for incremental code generators. 
It aims to be a powerful back-end for code generators that needs incremental generation and user intervention on generated code.
It provides the merge of user modification inside generated code using a Git repository.

Version
=======

Hannah is in early development state. Basics functionalities are present but it can't be distributed yet.w

TODO
====

* Add user modification description (maybe org.eclipse.jgit.diff.Edit is a good choice).
* Add conflict description (maybe org.eclipse.jgit.diff.Edit is a good choice).
* Add support for inter-active conflict resolution.
* Add tests for inter-active conflict resolution.
* Check how the Git repository can be transported by another SCM manager.
* Tests the generator on a real generator and checks how it supports modifications on lots of versions.

Getting Started
===============

The entry point of Hannah library is the class 'org.openflexo.hannah.IterativeFileGenerator'. 
It's use is described within the class JavaDoc.

Licence
=======

Hannah is distributed under the GLPv3 licence.
