package ox.puzzles

interface Day {
    fun part1s(): String {
        return part1().toString()
    }

    fun part2s(): String {
        return part2().toString()
    }

    fun part1(): Int {
        return 0
    }

    fun part2(): Int {
        return 0
    }

    private fun transformNameFull(name: String) = "${name.lowercase()}_input.txt"
    private fun transformNameSample(name: String, sampleNumber: Int? = null): String {
        if (sampleNumber == null) {
            return "${name.lowercase()}_sample_input.txt"
        }
        return "${name.lowercase()}_sample${sampleNumber}_input.txt"
    }

    private fun packageToPath(nameMap: (String) -> String): String {
        val fullName = this.javaClass.name
        val parts = fullName.split('.').toMutableList()
        val name = parts.last()

        parts.removeLast()
        parts.add(nameMap(name))
        return "/" + parts.joinToString("/")
    }

    fun getInputName() = packageToPath(::transformNameFull)
    fun getSampleInputName(sampleNumber: Int? = null) = packageToPath { transformNameSample(it, sampleNumber) }
    fun getResource(filename: String) = Day::class.java.getResourceAsStream(filename)!!
    fun getBufferedReader(filename: String) = getResource(filename).bufferedReader()
}