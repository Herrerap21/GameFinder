package com.gamefinder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var editGameName: EditText
    private lateinit var btnSearch: Button
    private lateinit var btnNotifications: Button
    private lateinit var txtPermissionStatus: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var txtError: TextView
    private lateinit var resultContainer: LinearLayout
    private lateinit var imageCover: ImageView
    private lateinit var txtTitle: TextView
    private lateinit var txtDeveloper: TextView
    private lateinit var txtPublisher: TextView
    private lateinit var txtGenre: TextView
    private lateinit var txtPlatform: TextView
    private lateinit var txtReleaseDate: TextView
    private lateinit var txtDescription: TextView

    private val client = OkHttpClient()
    private val apiUrl = "https://www.freetogame.com/api/games"
    private val notificationChannelId = "gamefinder_channel"
    private val notificationPermissionCode = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editGameName = findViewById(R.id.editGameName)
        btnSearch = findViewById(R.id.btnSearch)
        btnNotifications = findViewById(R.id.btnNotifications)
        txtPermissionStatus = findViewById(R.id.txtPermissionStatus)
        progressBar = findViewById(R.id.progressBar)
        txtError = findViewById(R.id.txtError)
        resultContainer = findViewById(R.id.resultContainer)
        imageCover = findViewById(R.id.imageCover)
        txtTitle = findViewById(R.id.txtTitle)
        txtDeveloper = findViewById(R.id.txtDeveloper)
        txtPublisher = findViewById(R.id.txtPublisher)
        txtGenre = findViewById(R.id.txtGenre)
        txtPlatform = findViewById(R.id.txtPlatform)
        txtReleaseDate = findViewById(R.id.txtReleaseDate)
        txtDescription = findViewById(R.id.txtDescription)

        createNotificationChannel()
        updatePermissionStatus()

        btnNotifications.setOnClickListener {
            requestNotificationPermission()
        }

        btnSearch.setOnClickListener {
            val gameName = editGameName.text.toString().trim()

            if (gameName.isEmpty()) {
                showError("Digite o nome de um jogo antes de pesquisar.")
            } else {
                searchGame(gameName)
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS

            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                txtPermissionStatus.text = "Notificações já estão ativadas."
                Toast.makeText(this, "Permissão já concedida.", Toast.LENGTH_SHORT).show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    notificationPermissionCode
                )
            }
        } else {
            txtPermissionStatus.text = "Notificações disponíveis nesta versão do Android."
            Toast.makeText(this, "Esta versão do Android não exige permissão em tempo de execução.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatePermissionStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS

            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                txtPermissionStatus.text = "Notificações ativadas. Você será avisado quando um jogo for encontrado."
            } else {
                txtPermissionStatus.text = "Ative as notificações para receber aviso quando um jogo for encontrado."
            }
        } else {
            txtPermissionStatus.text = "Notificações disponíveis nesta versão do Android."
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == notificationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                txtPermissionStatus.text = "Permissão concedida. Notificações ativadas."
                Toast.makeText(this, "Notificações ativadas.", Toast.LENGTH_SHORT).show()
            } else {
                txtPermissionStatus.text = "Permissão negada. O app continuará funcionando sem notificações."
                Toast.makeText(this, "Permissão negada. O app ainda pode pesquisar jogos.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun searchGame(gameName: String) {
        showLoading()

        thread {
            val request = Request.Builder()
                .url(apiUrl)
                .build()

            try {
                val response = client.newCall(request).execute()
                val json = response.body?.string()

                if (!response.isSuccessful || json.isNullOrEmpty()) {
                    runOnUiThread {
                        showError("Erro ao consultar a API.")
                    }
                    return@thread
                }

                val gamesArray = JSONArray(json)
                var foundGameIndex = -1

                for (i in 0 until gamesArray.length()) {
                    val game = gamesArray.getJSONObject(i)
                    val title = game.getString("title")

                    if (title.contains(gameName, ignoreCase = true)) {
                        foundGameIndex = i
                        break
                    }
                }

                runOnUiThread {
                    if (foundGameIndex == -1) {
                        showError("Jogo não encontrado. Tente outro nome, como Warframe, Dauntless ou Fortnite.")
                    } else {
                        val game = gamesArray.getJSONObject(foundGameIndex)
                        val title = game.getString("title")

                        txtTitle.text = title
                        txtDeveloper.text = "Desenvolvedora: ${game.getString("developer")}"
                        txtPublisher.text = "Publicadora: ${game.getString("publisher")}"
                        txtGenre.text = "Gênero: ${game.getString("genre")}"
                        txtPlatform.text = "Plataforma: ${game.getString("platform")}"
                        txtReleaseDate.text = "Lançamento: ${game.getString("release_date")}"
                        txtDescription.text = "Descrição: ${game.getString("short_description")}"

                        Glide.with(this)
                            .load(game.getString("thumbnail"))
                            .into(imageCover)

                        showResult()
                        sendGameFoundNotification(title)
                    }
                }

            } catch (e: IOException) {
                runOnUiThread {
                    showError("Falha de conexão. Verifique sua internet.")
                }
            } catch (e: Exception) {
                runOnUiThread {
                    showError("Resposta inesperada da API.")
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificações do GameFinder"
            val descriptionText = "Canal usado para avisar quando um jogo é encontrado."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(notificationChannelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendGameFoundNotification(gameTitle: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS

            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                txtPermissionStatus.text = "Jogo encontrado, mas a notificação não foi enviada porque a permissão foi negada."
                return
            }
        }

        val notification = NotificationCompat.Builder(this, notificationChannelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("GameFinder")
            .setContentText("Jogo encontrado: $gameTitle")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this).notify(1, notification)
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        txtError.visibility = View.GONE
        resultContainer.visibility = View.GONE
    }

    private fun showResult() {
        progressBar.visibility = View.GONE
        txtError.visibility = View.GONE
        resultContainer.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        resultContainer.visibility = View.GONE
        txtError.text = message
        txtError.visibility = View.VISIBLE
    }
}