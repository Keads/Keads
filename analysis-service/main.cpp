#include <iostream>
#include <mongocxx/client.hpp>
#include <mongocxx/instance.hpp>
#include <mongocxx/gridfs/bucket.hpp>
#include <bsoncxx/json.hpp>

int main() {
    mongocxx::instance instance{}; // Initialize MongoDB driver

    try {
        mongocxx::client client{mongocxx::uri{"mongodb://localhost:27017"}}; // Connect to MongoDB

        mongocxx::database db = client["your_database_name"]; // Replace with your database name
        mongocxx::gridfs::bucket bucket = db.gridfs_bucket();

        std::string fileId = "your_file_id"; // Replace with the actual file ID

        bsoncxx::stdx::optional<bsoncxx::document::value> fileDoc =
            bucket.find_one(bsoncxx::builder::stream::document{} << "_id" << bsoncxx::oid{fileId} << bsoncxx::builder::stream::finalize{});

        if (fileDoc) {
            bsoncxx::document::element sizeElement = fileDoc->view()["length"];
            if (sizeElement) {
                std::int64_t size = sizeElement.get_int64();
                std::cout << "File size: " << size << " bytes" << std::endl;
            } else {
                std::cout << "File size element not found" << std::endl;
            }
        } else {
            std::cout << "File not found" << std::endl;
        }
    } catch (const std::exception& e) {
        std::cerr << "MongoDB error: " << e.what() << std::endl;
    }

    return 0;
}
