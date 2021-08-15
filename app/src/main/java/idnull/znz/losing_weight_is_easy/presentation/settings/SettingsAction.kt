package idnull.znz.losing_weight_is_easy.presentation.settings

sealed class SettingsAction{
    data class ShowToast(val message: String) : SettingsAction()
}
