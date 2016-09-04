package global.helpers

import org.joda.time.{ LocalDate, DateTime }
import play.api.i18n.{ MessagesApi, Lang }

object I18nHelper {
  def plural(count: Int): String = count match {
    case 0 => ".0"
    case 1 => ".1"
    case _ => ".n"
  }
  def messagePlural(key: String, count: Int)(implicit lang: Lang, message: MessagesApi): String = count match {
    case 0 => message(key + ".0")
    case 1 => message(key + ".1")
    case _ => message(key + ".n", count)
  }
  def date(date: LocalDate)(implicit lang: Lang, message: MessagesApi): String = {
    date.toString(message("date"), lang.toLocale)
  }
  def date(date: LocalDate, suffix: String)(implicit lang: Lang, message: MessagesApi): String = {
    date.toString(message("date." + suffix), lang.toLocale)
  }
  def date(date: DateTime)(implicit lang: Lang, message: MessagesApi): String = {
    date.toString(message("date"), lang.toLocale)
  }
  def date(date: DateTime, suffix: String)(implicit lang: Lang, message: MessagesApi): String = {
    date.toString(message("date." + suffix), lang.toLocale)
  }
  def time(date: DateTime)(implicit lang: Lang, message: MessagesApi): String = {
    date.toString(message("time"), lang.toLocale)
  }
  def time(date: DateTime, suffix: String)(implicit lang: Lang, message: MessagesApi): String = {
    date.toString(message("time." + suffix), lang.toLocale)
  }
  def datetime(date: DateTime)(implicit lang: Lang, message: MessagesApi): String = {
    date.toString(message("datetime"), lang.toLocale)
  }
  def datetime(date: DateTime, suffix: String)(implicit lang: Lang, message: MessagesApi): String = {
    date.toString(message("datetime." + suffix), lang.toLocale)
  }
}
