package idnull.znz.losing_weight_is_easy.presentation.settings

data class SettingsViewState(
    val fetchStatus: FetchStatus,
    val nameText:String,
    val weightText:String ,
    val kCal :String
)

sealed class FetchStatus {
    object Update : FetchStatus()
    data class ShowError(val message: String) : FetchStatus()
    object Load : FetchStatus()
}
