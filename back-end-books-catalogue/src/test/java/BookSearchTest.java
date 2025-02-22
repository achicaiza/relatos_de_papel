import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class BookSearchTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        System.setProperty("webdriver.chrome.driver", "/Users/achicaiza/Documents/UNIR/chromedriver-mac-arm64/chromedriver"); // Ajusta la ruta de tu ChromeDriver
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Espera hasta 10 segundos por elementos visibles
    }
    @Test
    void testBuscarLibroPorTitulo() {
        // 1️⃣ Navegar a la URL de la aplicación
        driver.get("https://relatos-de-papel.vercel.app/main"); // Ajusta la URL de tu aplicación
//        driver.get("http://localhost:5173/main"); // Ajusta la URL de tu aplicación

        // 2️⃣ Encontrar el campo de búsqueda y escribir un título de libro
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search-bar")));
        searchBox.sendKeys("El Principito");
        searchBox.sendKeys(Keys.ENTER);

        // 3️⃣ Esperar y verificar que el resultado contiene el libro buscado
        WebElement bookTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("title-book")));
        String bookText = bookTitle.getText();

        // 4️⃣ Validar que el título sea correcto
        assertEquals("El Principito", bookText, "El título del libro no coincide con la búsqueda.");
    }

    @Test
    void testAgregarLibroAlCarrito() {
        // 1️⃣ Navegar a la página
        driver.get("https://relatos-de-papel.vercel.app/main");

        // 2️⃣ Esperar y hacer clic en el botón "Añadir al carrito"
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart")));
        addToCartButton.click();

        // 3️⃣ Esperar hasta que el badge del carrito se actualice
        WebElement badgeCar = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("badge-car")));
        WebElement badgeValue = badgeCar.findElement(By.className("MuiBadge-badge"));

        // 4️⃣ Verificar que el badge tenga el valor "1"
        String badgeText = badgeValue.getText();
        assertEquals("1", badgeText, "El número de ítems en el carrito no es correcto.");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
