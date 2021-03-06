Hannah
======

Hannah is a library that proposes a simple API for incremental code generators. 
It aims to be a powerful back-end for code generators that needs incremental 
generation and user intervention on generated code. It provides the merge of 
user modification inside generated code using a Git repository.

Version
=======

Hannah is in early development state. Basics functionalities are present but it
 can't be distributed yet.

TODO
====

* Add user modification description.
* Tests the generator on a real generator and checks how it supports 
  modifications on lots of versions.

Getting Started
===============

The entry point of Hannah library is the class 
`org.openflexo.hannah.IterativeFileGenerator`. It's use is described within the 
class JavaDoc.

```java
IterativeFileGenerator generator = new IterativeFileGenerator(output);
generator.start(ModificationHandler.accept);
generator.generate("file1.txt", "abc\ndef\nghi\n");
generator.end(ConflictHandler.user);
```

How does it work ?
==================

The idea behind Hannah is quite simple. It uses a Git repository to store all 
modifications made in the output folder. When starting a generation it commits 
modifications made by the user since last generation into the 'master' branch.
Then, it checks out the 'generation' branch. All generated file are then 
committed to 'generation' and the branch merged with-in the 'master' branch.

FAQ
===

* What is the `.hannah` folder ?

The `.hannah` folder is the `.git` folder renamed after a generation cycle. 
Renaming the folder also to use a Hannah inside a Git working copy.


Licence
=======

Hannah is distributed under the GLPv3 license.

Hannis is a part of the OpenFlexo project.

![OpenFlexo](http://openflexo.org/developers/wp-content/uploads/2011/09/openflexo-developers1.jpg)
