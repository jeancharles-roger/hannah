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

* Add user modification description isolated from Git DiffEntry.
* Add conflict description isolated from Git DiffEntry/Conflict.
* Add support for conflict resolution
* Add tests for inter-active conflict resolution
* Check how the Git repository can be transported by another SCM manager.

Licence
=======

Hannah is distributed under the GLPv3 licence.
