package net.blakelee.cryptochart

enum class Period (val value: Int) {
    DAY(0),
    WEEK(1),
    MONTH(2),
    QUARTER(3),
    SEMESTER(4),
    YEAR(5),
    ALL(6)
}