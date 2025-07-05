package sensicar.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Test

import org.junit.Rule
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class NavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testNavigationFromMenuToLeaderboard() {

        composeTestRule.waitUntil {
            composeTestRule
                .onAllNodesWithText("Leaderboards")
                .fetchSemanticsNodes().isNotEmpty()
        }


        composeTestRule.onNodeWithText("Leaderboards").performClick()


        composeTestRule.onNodeWithText("Menu")
            .assertExists()

    }

    @Test
    fun testNavigationFromMenuToLeaderboardAndBack() {
        // navigate to leaderboard
        composeTestRule.onNodeWithText("Leaderboards").performClick()

        // verify you  on leaderboard screen
        composeTestRule.onNodeWithText("Menu").assertExists()

        // navigate back to menu
        composeTestRule.onNodeWithText("Menu").performClick()

        // verify you are back on menu screen
        composeTestRule.onNodeWithText("Leaderboards").assertExists()
        composeTestRule.onNodeWithText("Play!").assertExists() // Assuming you have a Play button
    }

   /* @Test
    fun testNavigationFromMenuToGame() {
        // Wait for the menu screen to be displayed
        composeTestRule.waitUntil(timeoutMillis = 5000L) {
            try {
                composeTestRule
                    .onAllNodesWithText("Play!", ignoreCase = true)
                    .fetchSemanticsNodes().isNotEmpty()
            } catch (e: Exception) {
                false
            }
        }

        // Click on the Play! button
        composeTestRule.onNodeWithText("Play!", ignoreCase = true).performClick()

        // Verify we're on the game screen - adjust this based on what's visible on your GameScreen
        // For example, if there's a quit button or game UI element:
        composeTestRule.waitUntil(timeoutMillis = 5000L) {
            try {
                composeTestRule
                    .onAllNodesWithTag("quitButton") // or whatever button/text exists on GameScreen
                    .fetchSemanticsNodes().isNotEmpty()
            } catch (e: Exception) {
                false
            }
        }

        composeTestRule.onNodeWithTag("quitButton").assertExists()
    }*/

  /*  @Test
    fun testNavigationFromMenuToGame() {
        // Wait for the Play! button and click it
        composeTestRule.onNodeWithText("Play!", ignoreCase = true).assertExists().performClick()

        // Wait and assert that GameScreen is now visible
        composeTestRule.waitUntil(timeoutMillis = 5000L) {
            composeTestRule
                .onAllNodesWithTag("gameScreen")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("gameScreen").assertExists()
    }*/

}