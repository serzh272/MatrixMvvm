package ru.serzh272.matrixmvvm.extensions

import ru.serzh272.matrixmvvm.utils.Fraction

@ExperimentalUnsignedTypes
operator fun Int.plus(fr: Fraction): Fraction {
    return fr + this
}

@ExperimentalUnsignedTypes
operator fun Int.minus(fr: Fraction): Fraction {
    return (-fr) + this
}

@ExperimentalUnsignedTypes
operator fun Int.times(fr: Fraction): Fraction {
    return fr * this
}

@ExperimentalUnsignedTypes
operator fun Int.div(fr: Fraction): Fraction {
    return this * (fr.invert())
}