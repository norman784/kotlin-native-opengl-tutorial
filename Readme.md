# OpenGL tutorial written in Kotlin Native

The tutorial could be found [here](http://opengl-tutorial.org).

## Requirementes

- [GLFW](http://www.glfw.org)
- [GLEW](http://glew.sourceforge.net)

**MacOS**

```
brew install glfw glew
```

## Running

This will build and run every example.

```
gradle build
gradle run
```

*Need to check in the kotlin native gradle docs 
how to run only the desired example*

## TODO

**Basic tutorial**

- [x] 1 - Opening a window
- [x] 2 - Hello Triangle
- [ ] 3 - Matrices
- [ ] 4 - A Colored Cube
- [ ] 5 - A Textured Cube
- [ ] 6 - Keyboard and Mouse
- [ ] 7 - Model loading
- [ ] 8 - Basic shading

## Known issues

- For some reason the triangle is not drawed correctly, 
even when the vertices seems to be correct. 