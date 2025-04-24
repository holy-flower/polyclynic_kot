package com.example.polyclynic_kot

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import java.util.Locale

class SettingsDocFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.settings_doc_layout, container, false)

        val switchLanguage = view.findViewById<Switch>(R.id.languageSwitch)
        switchLanguage.setOnCheckedChangeListener { _, isChecked ->
            val newLocale = if (isChecked) "en" else "ru"
            updateLocale(requireContext(), newLocale)
            // Обновление UI без пересоздания активности
            view.post { updateView(view) }
        }

        return view
    }

    private fun updateLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        val resources = context.resources
        val metrics = resources.displayMetrics

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            resources.updateConfiguration(config, metrics)
        }
    }

    private fun updateView(view: View) {
        // Обновление текста или других элементов UI после изменения локали
        // Например:
        val switchLanguage = view.findViewById<Switch>(R.id.languageSwitch)
        switchLanguage.text = if (switchLanguage.isChecked) "English" else "Русский"
    }
}
