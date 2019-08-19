import com.beust.klaxon.Klaxon
import com.ebrithilcode.bomberman.client.Client
import com.ebrithilcode.bomberman.common.klaxon.PlayerActionMessage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestingKlaxon {

    @Test
    fun `test klaxon on string dataclass`() {
        data class EasyClass(val name : String)

        val toEncode = Klaxon().toJsonString(EasyClass("Philipp"))
        val decoded = Klaxon().parse<EasyClass>(toEncode)
        assertThat(decoded).isNotNull
        assertThat(decoded?.name).isEqualTo("Philipp")
    }

    @Test
    fun `test klaxon on class with set`() {
        data class WithSet(val values : Set<Int>)

        val toEncode = Klaxon().toJsonString(WithSet(setOf(3,1,4,5)))
        val decoded = Klaxon().parse<WithSet>(toEncode)
        assertThat(decoded).isNotNull
        assertThat(decoded?.values).isEqualTo(setOf(3,1,4,5))
        println("Encoded: $toEncode, decoded: $decoded")
    }

    @Test
    fun `test klaxon on class with enum set`() {
        data class WithSet(val values : Set<TestEnum>)

        val toEncode = Klaxon().toJsonString(WithSet(EnumSet.allOf(TestEnum::class.java)))
        val decoded = Klaxon().parse<WithSet>(toEncode)
        assertThat(decoded).isNotNull
        assertThat(decoded?.values).isEqualTo(setOf<TestEnum>(TestEnum.VALUE, TestEnum.OTHERVALUE))
        println("Encoded: $toEncode, decoded: $decoded")
    }

    @Test
    fun `test klaxon on enum type`() {
        data class WithEnum(val value : TestEnum)

        val toEncode = Klaxon().toJsonString(WithEnum(TestEnum.VALUE))
        val decoded = Klaxon().parse<WithEnum>(toEncode)
        assertThat(decoded).isNotNull
        assertThat(decoded?.value).isEqualTo(TestEnum.VALUE)
    }

    /*@Test
    fun `test klaxon on Player actions`() {
        val bytes = Klaxon().toJsonString(PlayerActionMessage(Client.currentPlayerActions)).toByteArray(Charsets.UTF_8)
        println("Sending: ${String(bytes)}")
        println("That decoded: ${Klaxon().parse<PlayerActionMessage>(String(bytes))}")
        val packet = DatagramPacket(bytes, bytes.size, InetSocketAddress(
            Client.REMOTE_IP,
            Client.remoteConnectionPort
        ))
    }*/



}
enum class TestEnum {
    VALUE, OTHERVALUE
}