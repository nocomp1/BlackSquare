

cmake_minimum_required(VERSION 3.4.1)
add_library( # Specifies the name of the library.
        native-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        src/main/cpp/native-lib.cpp )
### INCLUDE OBOE LIBRARY ###

# Set the path to the Oboe library directory
set (OBOE_DIR /Users/3006142/Desktop/oboe)

# Add the Oboe library as a subproject. Since Oboe is an out-of-tree source library we must also
# specify a binary directory
add_subdirectory(${OBOE_DIR} ./oboe-bin)

# Include the Oboe headers and shared sample code
include_directories(${OBOE_DIR}/include)

target_link_libraries(native-lib log oboe)
# Debug utilities
set (DEBUG_UTILS_PATH "${OBOE_DIR}/samples/debug-utils")
set (DEBUG_UTILS_SOURCES ${DEBUG_UTILS_PATH}/trace.cpp)
include_directories(${DEBUG_UTILS_PATH})

