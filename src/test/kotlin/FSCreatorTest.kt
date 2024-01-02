import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

internal class FSCreatorTest {
    private val testDir: File = File("src${File.separator}main${File.separator}resources")

    @Test
    fun `File Path Not Found Exception`() {

        // Create a temporary directory
        val tempDir = File(testDir, "path_not_found")
        tempDir.mkdir()


        val fsCreator=FSCreator()

        val file:FSEntry=FSFile(name = "text.txt", content = "content")

        val exception = assertFailsWith<EntryException> {
            fsCreator.create(entryToCreate = file, destination = "notExisting")
        }

        val expectedOutput="Path notExisting not found"

        assertEquals(expectedOutput, exception.message)
        tempDir.deleteRecursively()

    }

    @Test
    fun `File In A File Exception`() {

        // Create a temporary directory
        val tempDir = File(testDir, "file_in_file")
        tempDir.mkdir()

        val fsCreator=FSCreator()
        val file:FSEntry=FSFile(name = "text.txt", content = "content")
        fsCreator.create(entryToCreate = file, destination = "$testDir${File.separator}file_in_file")

        val exception = assertFailsWith<EntryException> {
            fsCreator.create(entryToCreate = file, destination = "$testDir${File.separator}file_in_file${File.separator}text.txt")
        }
        val expectedOutput="Can't create entity in a file"
        assertEquals(expectedOutput, exception.message)
        tempDir.deleteRecursively()
    }
    @Test
    fun `File Already Exists Exception`() {

        // Create a temporary directory
        val tempDir = File(testDir, "file_already_exists")
        tempDir.mkdir()


        val fsCreator=FSCreator()
        val file:FSEntry=FSFile(name = "text.txt", content = "content")
        fsCreator.create(entryToCreate = file, destination = "$testDir${File.separator}file_already_exists")
        val exception = assertFailsWith<EntryException> {
            fsCreator.create(entryToCreate = file, destination = "$testDir${File.separator}file_already_exists")
        }

        val expectedOutput = "Can't create text.txt,it already exists"
        assertEquals(expectedOutput, exception.message)

        tempDir.deleteRecursively()

    }

    @Test
    fun `Directory Path Not Found Exception`() {

        // Create a temporary directory
        val tempDir = File(testDir, "directory_path_not_found")
        tempDir.mkdir()


        val fsCreator=FSCreator()

        val file1=FSFile(name = "text1.txt", content = "content1")
        val file2=FSFile(name = "text2.txt", content = "content2")
        val file3=FSFile(name = "text3.txt", content = "content3")
        val files:List<FSFile> = listOf( file1,file2,file3)
        val folder:FSEntry=FSFolder(name = "FSFolder", files = files)

        val exception = assertFailsWith<EntryException> {
            fsCreator.create(entryToCreate = folder, destination = "notExisting")
        }
        val expectedOutput = "Path notExisting not found"

        assertEquals(expectedOutput, exception.message)

        tempDir.deleteRecursively()

    }

    @Test
    fun `Directory Already Exists Exception`() {

        // Create a temporary directory
        val tempDir = File(testDir, "directory_already_exists")
        tempDir.mkdir()


        val fsCreator=FSCreator()

        val file1=FSFile(name = "text1.txt", content = "content1")
        val file2=FSFile(name = "text2.txt", content = "content2")
        val file3=FSFile(name = "text3.txt", content = "content3")
        val files:List<FSFile> = listOf( file1,file2,file3)
        val folder:FSEntry=FSFolder(name = "FSFolder", files = files)
        fsCreator.create(entryToCreate = folder, destination = "$testDir${File.separator}directory_already_exists")

        val exception = assertFailsWith<EntryException> {
            fsCreator.create(entryToCreate = folder, destination = "$testDir${File.separator}directory_already_exists")
        }
        val expectedOutput="Can't create FSFolder,it already exists"
        assertEquals(expectedOutput, exception.message)

        tempDir.deleteRecursively()

    }
    @Test
    fun `Directory In A File Exception`() {

        // Create a temporary directory
        val tempDir = File(testDir, "directory_in_file")
        tempDir.mkdir()


        val fsCreator=FSCreator()

        val file1=FSFile(name = "text1.txt", content = "content1")
        val file2=FSFile(name = "text2.txt", content = "content2")
        val file3=FSFile(name = "text3.txt", content = "content3")
        val files:List<FSFile> = listOf( file1,file2,file3)
        val folder:FSEntry=FSFolder(name = "FSFolder", files = files)

        val file=FSFile(name = "text.txt", content = "content1")
        fsCreator.create(file,"$testDir${File.separator}directory_in_file")

        val exception = assertFailsWith<EntryException> {
            fsCreator.create(folder,"$testDir${File.separator}directory_in_file${File.separator}text.txt")
        }
        assertEquals("Can't create entity in a file", exception.message)

        tempDir.deleteRecursively()

    }
    @Test
    fun `Nested Directory Structure`() {

        // Create a temporary directory
        val tempDir = File(testDir, "nested_directory")
        tempDir.mkdir()


        val fsCreator = FSCreator()

        val file1 = FSFile("file1.txt", "Content1")
        val file2 = FSFile("file2.txt", "Content2")

        val subFolder1 = FSFolder("subFolder1", listOf(file2))
        val subFolder2 = FSFolder("subFolder2", listOf(subFolder1, file1))

        val rootFolder = FSFolder("root", listOf(subFolder1,subFolder2))

        val destinationPath = "$testDir${File.separator}nested_directory"

        fsCreator.create(rootFolder, destinationPath)

        val rootFolderFile = File(destinationPath, "root")
        assertTrue(rootFolderFile.exists())
        assertTrue(rootFolderFile.isDirectory)

        val subFolder2File = File(rootFolderFile, "subFolder2")
        assertTrue(subFolder2File.exists())
        assertTrue(subFolder2File.isDirectory)

        val subFolder1File = File(subFolder2File, "subFolder1")
        assertTrue(subFolder1File.exists())
        assertTrue(subFolder1File.isDirectory)

        val file1File = File(subFolder2File, "file1.txt")
        assertTrue(file1File.exists())
        assertTrue(file1File.isFile)
        assertEquals("Content1", file1File.readText())

        val file2File = File(subFolder1File, "file2.txt")
        assertTrue(file2File.exists())
        assertTrue(file2File.isFile)
        assertEquals("Content2", file2File.readText())

        tempDir.deleteRecursively()

    }

    @Test
    fun `Empty Directory`() {

        // Create a temporary directory
        val tempDir = File(testDir, "empty_directory")
        tempDir.mkdir()

        val fsCreator = FSCreator()

        val emptyFolder = FSFolder("emptyFolder", emptyList())
        val destinationPath = "$testDir${File.separator}empty_directory"

        fsCreator.create(emptyFolder, destinationPath)

        val emptyFolderFile = File(destinationPath, "emptyFolder")
        assertTrue(emptyFolderFile.exists())
        assertTrue(emptyFolderFile.isDirectory)
        assertTrue(emptyFolderFile.listFiles()?.isEmpty() ?: false)

        tempDir.deleteRecursively()

    }

    @Test
    fun `Mixed File and Directory Structure`() {

        // Create a temporary directory
        val tempDir = File(testDir, "mixed_file_and_directory")
        tempDir.mkdir()

        val fsCreator = FSCreator()

        val file1 = FSFile("file1.txt", "Content1")
        val file2 = FSFile("file2.txt", "Content2")
        val subFolder1 = FSFolder("subFolder1", listOf(file2))
        val subFolder2 = FSFolder("subFolder2", listOf(subFolder1, file1))
        val rootFolder = FSFolder("rootMixed", listOf(subFolder2, file1))

        val destinationPath = "$testDir${File.separator}mixed_file_and_directory"

        fsCreator.create(rootFolder, destinationPath)

        val rootFolderFile = File(destinationPath, "rootMixed")
        assertTrue(rootFolderFile.exists())
        assertTrue(rootFolderFile.isDirectory)

        val subFolder2File = File(rootFolderFile, "subFolder2")
        assertTrue(subFolder2File.exists())
        assertTrue(subFolder2File.isDirectory)

        val subFolder1File = File(subFolder2File, "subFolder1")
        assertTrue(subFolder1File.exists())
        assertTrue(subFolder1File.isDirectory)

        val file1File = File(subFolder2File, "file1.txt")
        assertTrue(file1File.exists())
        assertTrue(file1File.isFile)
        assertEquals("Content1", file1File.readText())

        val file2File = File(subFolder1File, "file2.txt")
        assertTrue(file2File.exists())
        assertTrue(file2File.isFile)
        assertEquals("Content2", file2File.readText())

        val file1Duplicate = File(rootFolderFile, "file1.txt")
        assertTrue(file1Duplicate.exists())
        assertTrue(file1Duplicate.isFile)
        assertEquals("Content1", file1Duplicate.readText())

        tempDir.deleteRecursively()

    }

    // These tests should pass if the file system is case-sensitive.

//    @Test
//    fun `create - Directory Case Sensitivity`() {
//        // Create a temporary directory
//        val tempDir = File(testDir, "directory_case_sensitive")
//        tempDir.mkdir()
//        val fsCreator=FSCreator()
//
//        val file1=FSFile(name = "text1.txt", content = "content1")
//        val file2=FSFile(name = "text2.txt", content = "content2")
//        val file3=FSFile(name = "text3.txt", content = "content3")
//        val files:List<FSFile> = listOf( file1,file2,file3)
//        val folder:FSEntry=FSFolder(name = "FSFoldER", files = files)
//        fsCreator.create(entryToCreate = folder, destination = "$testDir${File.separator}directory_case_sensitive")
//
//        val exception = assertFailsWith<EntryException> {
//            fsCreator.create(entryToCreate = folder, destination = "$testDir${File.separator}directory_case_sensitive${File.separator}FsFoldeR")
//        }
//
//        assertEquals("Path src\\main\\resources\\test\\Folder not found", exception.message)
//        tempDir.deleteRecursively()
//    }
//    @Test
//    fun `create - File Case Sensitivity`() {
//        // Create a temporary directory
//        val tempDir = File(testDir, "file_case_sensitive")
//        tempDir.mkdir()
//        val fsCreator=FSCreator()
//        val file:FSEntry=FSFile(name = "text.txt", content = "content")
//
//        val folder:FSEntry=FSFolder(name = "folderSensitive", files = listOf())
//        fsCreator.create(entryToCreate = folder, destination = "$testDir${File.separator}file_case_sensitive")
//
//        val exception = assertFailsWith<FileException> {
//            fsCreator.create(entryToCreate = file, destination = "$testDir${File.separator}file_case_sensitive${File.separator}FolDerSEnsitivE")
//        }
//        val expectedOutput="Path $testDir${File.separator}file_case_sensitive${File.separator}FolDerSEnsitivE not found"
//
//        assertEquals(expectedOutput, exception.message)
//        tempDir.deleteRecursively()
//    }
}