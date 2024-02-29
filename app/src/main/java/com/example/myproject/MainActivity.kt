package com.example.myproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val linkToBank: CardView = findViewById(R.id.cardBank)
        val linkToChart: CardView = findViewById(R.id.cardChart)
        val linkToCourse: CardView = findViewById(R.id.cardCourse)
        val linkToPercent: CardView = findViewById(R.id.cardPercent)
        val linkToAboutAs: CardView = findViewById(R.id.cardAboutAs)

        linkToBank.setOnClickListener{
            val intent = Intent(this, BankActivity::class.java)
            startActivity(intent)
        }

        linkToChart.setOnClickListener{
            val intent = Intent(this, ChartActivity::class.java)
            startActivity(intent)
        }

        linkToCourse.setOnClickListener{
            val intent = Intent(this, CourseActivity::class.java)
            startActivity(intent)
        }

        linkToPercent.setOnClickListener{
            val intent = Intent(this, PercentActivity::class.java)
            startActivity(intent)
        }

        linkToAboutAs.setOnClickListener{
            val intent = Intent(this, AboutasActivity::class.java)
            startActivity(intent)
        }
    }
}