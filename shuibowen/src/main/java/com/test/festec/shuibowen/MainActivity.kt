package com.test.festec.shuibowen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.test.festec.shuibowen.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.singleBtn.setOnClickListener {
            val intent = Intent(this,SingleActivity::class.java)
            startActivity(intent)
        }

        binding.multipleBtn.setOnClickListener {
            val intent = Intent(this,MultipleActivity::class.java)
            startActivity(intent)
        }

    }

}