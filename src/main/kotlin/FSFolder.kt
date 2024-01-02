class FSFolder(name:String,  private val files:List<FSEntry>):FSEntry(name)
{
    fun getFiles():List<FSEntry> = files
}