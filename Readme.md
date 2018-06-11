# OpenGL tutorial written in Kotlin Native

The tutorial could be found [here](http://opengl-tutorial.org).

## Requirementes

- [GLFW](http://www.glfw.org)
- [GLEW](http://glew.sourceforge.net)

**MacOS**

```
brew install glfw glew
```

**Ubuntu**

*Not tested*

```
sudo apt-get install libglfw3 libglfw3-dev libglew-dev
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

## CMake script to copy resources

```cmake
add_custom_target(copy-resources ALL
        COMMAND cmake -E copy_directory ${CMAKE_SOURCE_DIR}/src/resources ${CMAKE_BINARY_DIR}/resources
        DEPENDS ${intown})
```