abstract class FSEntry(protected var name: String) {
    @JvmName("getFSEntryName")
    fun getName(): String = name
}
