cmake_minimum_required(VERSION 3.12)
project(YourProjectName)

# Set the minimum required C++ standard
set(CMAKE_CXX_STANDARD 11)

# Define the path where the MongoDB C++ driver will be installed
set(MONGOCXX_INSTALL_DIR ${CMAKE_BINARY_DIR}/mongocxx-install)

# Configure the ExternalProject to fetch and build the MongoDB C++ driver
include(ExternalProject)
ExternalProject_Add(mongocxx_external
    GIT_REPOSITORY https://github.com/mongodb/mongo-cxx-driver.git
    GIT_TAG releases/stable
    CMAKE_ARGS -DCMAKE_INSTALL_PREFIX=${MONGOCXX_INSTALL_DIR} -DCMAKE_BUILD_TYPE=Release
    BUILD_COMMAND ${CMAKE_COMMAND} --build .
)

# Specify the include directories and library dependencies for mongocxx
set(MONGOCXX_INCLUDE_DIR ${MONGOCXX_INSTALL_DIR}/include/mongocxx/v_noabi)
set(MONGOCXX_LIB_DIR ${MONGOCXX_INSTALL_DIR}/lib)
set(MONGOCXX_LIBRARIES ${MONGOCXX_LIB_DIR}/libmongocxx.so ${MONGOCXX_LIB_DIR}/libbsoncxx.so)

# Add your project's executable or library
add_executable(your_target_name your_source_files.cpp)
target_include_directories(your_target_name PRIVATE ${MONGOCXX_INCLUDE_DIR})
target_link_libraries(your_target_name PRIVATE ${MONGOCXX_LIBRARIES})

# Add a dependency on the ExternalProject target to ensure it builds before your project
add_dependencies(your_target_name mongocxx_external)
