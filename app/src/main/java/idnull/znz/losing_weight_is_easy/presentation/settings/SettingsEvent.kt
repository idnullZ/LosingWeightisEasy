package idnull.znz.losing_weight_is_easy.presentation.settings

sealed class SettingsEvent {
    object ScreenShown : SettingsEvent()
    object ClickUpdate : SettingsEvent()
}
