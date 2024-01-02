class FSFile( name:String, private val content:String) :FSEntry(name) {
    fun getContent():String=content
}