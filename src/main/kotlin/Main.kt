fun main(args: Array<String>) {
    try {
     val fsCreator=FSCreator()
      println("File")
      val file:FSEntry=FSFile("text.txt","data")
      fsCreator.create(file,"new_folder")
      fsCreator.create(file,"src\\main\\resources")
      fsCreator.create(file,"src\\main\\resources\\text.txt")
      fsCreator.create(file,"src\\main\\resources\\not_found")

      println("\nFolder")
      val file1=FSFile("text1.txt","data1")
      val file2=FSFile("text2.txt","data2")
      val file3=FSFile("text3.txt","data3")
      val list:List<FSFile> = listOf( file1,file2,file3)
      val folder:FSEntry=FSFolder("FSFolder",list)
      fsCreator.create(folder,"new_folder")
      fsCreator.create(folder,"src\\main\\resources")
      fsCreator.create(folder,"src\\main\\resources\\FSFolder")
      fsCreator.create(folder,"src\\main\\resources\\text.txt")
      fsCreator.create(folder,"src\\main\\resources\\not_found")

    } catch (e: EntryException) {
     println(e.message)
    } catch (e: Exception) {
     println(e.message)
    }

}