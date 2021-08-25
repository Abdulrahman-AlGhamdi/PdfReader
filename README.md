# PDF Reader ![Minimum API level](https://img.shields.io/badge/API-23+-yellow)

PDF reader library that is useful for viewing pdf pages and have the ability to choose a specific page and zoom it

This library helps the user to implement :
* Showing the PDF and scroll through the pages.
* Choose specific PDF page to zoom it.

## Installation

### Repositories

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
### Dependency [![](https://jitpack.io/v/Abdulrahman-AlGhamdi/PdfReader.svg)](https://jitpack.io/#Abdulrahman-AlGhamdi/PdfReader)
```groovy
implementation 'com.github.Abdulrahman-AlGhamdi:PdfReader:Tag'
```

## Usage

* First  : 
    * call `PdfReaderManager` helper class
    * Get the instance of the class

```kotlin
val pdfReaderManager = PdfReaderManager.getManagerInstance()
```

* Second : 
    * Call `showPdf` function
    * In the constructor provide the inputstream of the pdf file

```kotlin
pdfReaderManager.showPdf(inputStream = pdfFileInputStream)
```

## License

```
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```
