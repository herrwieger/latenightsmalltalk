Latenight Smalltalk currently consists of two eclipse projects:
  * Smalltalk
  * Smalltalk Eclipse

**Smalltalk** contains the compiler. Actually it contains two compilers:
  * JavassistUniverse translates Smalltalk classes directly to bytecode.
  * JavasourceUniverse translates Smalltalk classes to java source code.
Additionally **Smalltalk** is an eclipse plugin.

**Smalltalk Eclipse** is an Eclipse plugin which uses the **Smalltalk** plugin. **Smalltalk Eclipse** is based on the [Eclipse DLTK projects](http://www.eclipse.org/dltk/) infrastructure and provides the first parts of a Smalltalk IDE. Currently it provides a Smalltalk Editor with semantic highlighting. Thanks to the DLTK infrastructure, it automatically provide also a class outline view.