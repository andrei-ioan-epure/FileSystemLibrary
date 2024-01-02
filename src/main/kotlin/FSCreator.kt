import java.io.File

class FSCreator {

    private fun createEntity(entryToCreate: FSEntry, directory: File) {

        when (entryToCreate) {
            is FSFile -> {
                directory.mkdirs()
                File(directory, entryToCreate.getName()).writeText(entryToCreate.getContent())
            }
            is FSFolder -> {
                val folder = File(directory, entryToCreate.getName())
                folder.mkdirs()
                entryToCreate.getFiles().forEach { createEntity(it, folder) }
            }
        }
    }

    fun create(entryToCreate: FSEntry, destination: String) {

        val destinationInstance = File(destination)

        when (destinationInstance.exists()) {
                true -> {
                    if (destinationInstance.isFile) {

                        throw  EntryException("Can't create entity in a file")

                    } else if (destinationInstance.isDirectory) {
                        val exist = destinationInstance.listFiles()
                            ?.any { (it.isDirectory || it.isFile) && it.name == entryToCreate.getName() }

                        if (exist != null && exist == true) {
                            throw EntryException("Can't create ${entryToCreate.getName()},it already exists")
                        }

                        createEntity(entryToCreate, destinationInstance)
                    }
                }
                false -> {
                    throw  EntryException("Path ${destinationInstance.path} not found")
                }
            }


    }
}