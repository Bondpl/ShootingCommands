// ...existing code...

var delayInputText by remember { mutableStateOf("0") }
var delayMs by remember { mutableStateOf(0L) }

Text(
    text = "Random Shooting Commands",
    style = MaterialTheme.typography.headlineMedium,
    modifier = Modifier
        .align(Alignment.CenterHorizontally)
        .padding(bottom = 32.dp)
)

Column(
    modifier = modifier
        .fillMaxSize()
        .padding(16.dp),
    horizontalAlignment = Alignment.Start,
    verticalArrangement = Arrangement.Center
) {
    OutlinedTextField(
        value = delayInputText,
        onValueChange = {
            val filtered = it.filter { c -> c.isDigit() }
            val value = filtered.toLongOrNull() ?: 0L
            val limited = value.coerceAtMost(5000L)
            delayInputText = if (filtered.isEmpty()) "0" else limited.toString()
            // delayMs NIE jest tu ustawiany!
        },
        label = { Text("Delay (ms, max 5000)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )

    Button(
        onClick = {
            // Ustaw delayMs dopiero po kliknięciu Start
            delayMs = delayInputText.toLongOrNull()?.coerceAtMost(5000L) ?: 0L
            playRandomAudioLoopWithDelay(context, audioFiles, delayMs) { player ->
                mediaPlayer?.release()
                mediaPlayer = player
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text("Start")
    }
}
