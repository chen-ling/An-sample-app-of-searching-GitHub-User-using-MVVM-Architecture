package ccl.exercise.githubsearch.model

sealed class Item(val type: Type) {

    enum class Type {
        User,
        LOADING
    }

    data class UserItem(val user: User) : Item(Type.User)

    data class LoadingItem(val progressType: Int = 0) : Item(Type.LOADING)
}