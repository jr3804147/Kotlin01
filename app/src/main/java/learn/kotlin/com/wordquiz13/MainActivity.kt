package learn.kotlin.com.wordquiz13

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.xmlpull.v1.XmlPullParser
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.*

class MainActivity : AppCompatActivity() {
    private val MODE_toefl01 = 3
    private val MODE_toefl02 = 4
    private val MODE_caihong_biz62 = 5
    private val mQuestions = mutableListOf<Question>()
    private var mCurrentNumber: Int = 0
    private val mAnswer = mutableListOf<Int>()
    private var mMode: Int = 5
    private var CntText01 = "Bye World!"
    private var CntRight = 1000
    private var CntLeft = 2000
    private var saveCntRight = CntRight
    private var saveCntLeft = CntLeft
    private var getAnswer3 = "getAnswer3"

    // 시스템의 임시 디렉토리명을 획득, 운영체제마다 다름
    // Windows 10의 경우 C:\Users\{user}\AppData\Local\Temp\
    private var pathname : String? = System.getProperty("java.io.tmpdir")
    private var someFile = File(pathname + "some-file.txt")
/*
    //private val fileName = "src/resources/myfile.txt"
    private val fileName = pathname + "myfile.txt"
    private val myfile = File(fileName)
    private val lines: List<String> = File(fileName).readLines()
*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setQuestion()
        updateUi()
        setButton()
        getAnswer2()

        if (!someFile.exists())
        {
            // 문자열을 앞서 지정한 경로에 파일로 저장, 저장시 캐릭터셋은 기본값인 UTF-8으로 저장
            // 이미 파일이 존재할 경우 덮어쓰기로 저장
            // 파일이 아닌 디렉토리이거나 기타의 이유로 저장이 불가능할 경우 FileNotFoundException 발생
            //someFile.writeText("가나다라마바사")
            // 저장시 캐릭터셋으로 EUC-KR을 명시하여 저장
            someFile.writeText(saveCntRight.toString()+saveCntLeft.toString(), Charset.forName("EUC-KR"))
        }
        // 파일을 문자열로 읽음, 캐릭터셋은 기본값은 UTF-8로 로드
        // 파일이 존재하지 않을 경우 FileNotFoundException 발생
        // 파일을 한 번에 모두 읽기 때문에 2GB 크기 제한을 가짐
        //someFile.readText(Charset.forName("UTF-8"))
        // 캐릭터셋으로 EUC-KR을 명시하여 읽음
        CntText01 = someFile.readText(Charset.forName("EUC-KR"))
        //TV_CntTest01.setText("FileText : ${CntText01}") //for Debugging

        CntRight = CntText01.substring(1,4).toInt()
        CntLeft = CntText01.substring(5,8).toInt()
        TV_CntRight.setText("정답수 : ${CntRight}")
        TV_CntLeft.setText("오답수 : ${CntLeft}")
        mode_button.setText(R.string.caihong_biz62_mode)
/*
        myfile.printWriter().use { out ->
            out.print("Head ")
            out.println("Tail")
            out.println("Second line")
        }
        CntText01 = myfile.readText(Charset.forName("EUC-KR"))
        TV_CntTest01.setText("FileText : ${CntText01}") //for Debugging
        //lines.forEach { line -> println(line) }
        lines.forEach { line -> TV_CntTest01.setText(line) }
*/
    }

    private fun getAnswer2(){
        submit2.setOnClickListener{
            getAnswer3 = editText2.text.toString()
            Toast.makeText(this, getAnswer3, Toast.LENGTH_SHORT).show()

            if (mQuestions[mCurrentNumber].answerData == getAnswer3) {
                Toast.makeText(applicationContext, R.string.answer_true, Toast.LENGTH_SHORT).show()
                CntRight++;
                saveCntRight = CntRight + 1000
                someFile.writeText(saveCntRight.toString()+saveCntLeft.toString(), Charset.forName("EUC-KR"))
                TV_CntRight.setText("정답수 : ${CntRight}")
            } else {
                Toast.makeText(applicationContext, R.string.answer_false, Toast.LENGTH_SHORT).show()
                CntLeft++
                saveCntLeft = CntLeft + 2000
                someFile.writeText(saveCntRight.toString()+saveCntLeft.toString(), Charset.forName("EUC-KR"))
                TV_CntLeft.setText("오답수 : ${CntLeft}")
            }
            next_button.isEnabled = true
        }
    }

    private fun setQuestion() {
        lateinit var questionValue: String
        lateinit var answerValue: String
        lateinit var questionAttribute: String
        lateinit var answerAttribute: String
        var id: Int = 0

        if (mMode == MODE_toefl01) {
            questionAttribute = "korean2"
            answerAttribute = "english2"
            id = R.xml.toefl01
        } else if (mMode == MODE_toefl02) {
            questionAttribute = "korean3"
            answerAttribute = "english3"
            id = R.xml.toefl02
        } else if (mMode == MODE_caihong_biz62) {
            questionAttribute = "chinese4"
            answerAttribute = "pinyin4"
            id = R.xml.caihong_biz62
        }

        var xml = applicationContext.resources.getXml(id)

        try {
            var eventType = xml.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xml.name == "string")
                    {
                        for (i in 0 until xml.attributeCount) {
                            if (xml.getAttributeName(i) == questionAttribute) {
                                questionValue = xml.getAttributeValue(i)
                            } else if (xml.getAttributeName(i) == answerAttribute) {
                                answerValue = xml.getAttributeValue(i)
                            }
                        }
                        mQuestions.add(Question(questionValue, answerValue))
                    }
                }
                eventType = xml.next()
            }
/*        } catch (e: XmlPullParserException) {
            e.printStackTrace()*/
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun showQuestion() {
        if (mMode == MODE_toefl01) {
            val random = Random()
            mCurrentNumber = random.nextInt(mQuestions.size)
            question_text.setText("${mCurrentNumber + 1}" + ". " + resources.getString(R.string.toefl01_title))
        } else if (mMode == MODE_toefl02) {
            val random = Random()
            mCurrentNumber = random.nextInt(mQuestions.size)
            question_text.setText("${mCurrentNumber + 1}" + ". " + resources.getString(R.string.toefl02_title))
        } else if (mMode == MODE_caihong_biz62) {
            val random = Random()
            mCurrentNumber = random.nextInt(mQuestions.size)
            question_text.setText("${mCurrentNumber + 1}" + ". " + resources.getString(R.string.caihong_biz62_title))
        }
        question_word_text.setText(mQuestions[mCurrentNumber].questionData)

        answer_one.isEnabled = true
        answer_two.isEnabled = true
        answer_three.isEnabled = true
        next_button.isEnabled = false
    }

    private fun updateUi() {
        showQuestion()
        setAnswerData()
        setAnswerButtonText()
    }

    private fun setButton() {
        mode_toZero.setOnClickListener {
            saveCntRight = 1000
            saveCntLeft = 2000
            someFile.writeText(saveCntRight.toString()+saveCntLeft.toString(), Charset.forName("EUC-KR"))
            CntText01 = someFile.readText(Charset.forName("EUC-KR"))
            CntRight = CntText01.substring(1,4).toInt()
            CntLeft = CntText01.substring(5,8).toInt()
            TV_CntRight.setText("정답수 : ${CntRight}")
            TV_CntLeft.setText("오답수 : ${CntLeft}")
        }

        mode_button.setOnClickListener {
            if (mMode == MODE_toefl01) {
                mMode = 4
                mode_button.setText(R.string.toefl02_mode)
            } else if (mMode == MODE_toefl02) {
                mMode = 5
                mode_button.setText(R.string.caihong_biz62_mode)
            } else if (mMode == MODE_caihong_biz62) {
                mMode = 3
                mode_button.setText(R.string.toefl01_mode)
            }
            mQuestions.clear()
            setQuestion()
            updateUi()
        }

        next_button.setOnClickListener {
//            mCurrentNumber = (mCurrentNumber + 1) % mQuestions.size
            val random = Random()
            mCurrentNumber = random.nextInt(mQuestions.size)
            updateUi()
        }

        answer_one.setOnClickListener {
            if (mQuestions[mCurrentNumber].answerData == mQuestions[mAnswer.get(0)].answerData) {
                Toast.makeText(applicationContext, R.string.answer_true, Toast.LENGTH_SHORT).show()
                CntRight++
                saveCntRight = CntRight + 1000
                someFile.writeText(saveCntRight.toString()+saveCntLeft.toString(), Charset.forName("EUC-KR"))
                TV_CntRight.setText("정답수 : ${CntRight}")
            } else {
                Toast.makeText(applicationContext, R.string.answer_false, Toast.LENGTH_SHORT).show()
                CntLeft++
                saveCntLeft = CntLeft + 2000
                someFile.writeText(saveCntRight.toString()+saveCntLeft.toString(), Charset.forName("EUC-KR"))
                TV_CntLeft.setText("오답수 : ${CntLeft}")
            }
            answer_one.isEnabled = false
            next_button.isEnabled = true
        }

        answer_two.setOnClickListener {
            if (mQuestions[mCurrentNumber].answerData == mQuestions[mAnswer.get(1)].answerData) {
                Toast.makeText(applicationContext, R.string.answer_true, Toast.LENGTH_SHORT).show()
                CntRight++;
                saveCntRight = CntRight + 1000
                someFile.writeText(saveCntRight.toString()+saveCntLeft.toString(), Charset.forName("EUC-KR"))
                TV_CntRight.setText("정답수 : ${CntRight}")
            } else {
                Toast.makeText(applicationContext, R.string.answer_false, Toast.LENGTH_SHORT).show()
                CntLeft++
                saveCntLeft = CntLeft + 2000
                someFile.writeText(saveCntRight.toString()+saveCntLeft.toString(), Charset.forName("EUC-KR"))
                TV_CntLeft.setText("오답수 : ${CntLeft}")
            }
            answer_two.isEnabled = false
            next_button.isEnabled = true
        }

        answer_three.setOnClickListener {
            if (mQuestions[mCurrentNumber].answerData == mQuestions[mAnswer.get(2)].answerData) {
                Toast.makeText(applicationContext, R.string.answer_true, Toast.LENGTH_SHORT).show()
                CntRight++;
                saveCntRight = CntRight + 1000
                someFile.writeText(saveCntRight.toString()+saveCntLeft.toString(), Charset.forName("EUC-KR"))
                TV_CntRight.setText("정답수 : ${CntRight}")
            } else {
                Toast.makeText(applicationContext, R.string.answer_false, Toast.LENGTH_SHORT).show()
                CntLeft++
                saveCntLeft = CntLeft + 2000
                someFile.writeText(saveCntRight.toString()+saveCntLeft.toString(), Charset.forName("EUC-KR"))
                TV_CntLeft.setText("오답수 : ${CntLeft}")
            }
            answer_three.isEnabled = false
            next_button.isEnabled = true
        }
    }

    private fun setAnswerData() {
        var isDuplicated = false
        mAnswer.clear()
        mAnswer.add(0, -1)
        mAnswer.add(1, -1)
        mAnswer.add(2, -1)
        mAnswer.add(3, -1)

        var count = 0
        val random = Random()

        var temp: Int
        while (true) {
            temp = random.nextInt(mQuestions.size - 1)
            if (temp == mCurrentNumber) {
                continue
            }

            for (i in 0..2) {
                if (temp == mAnswer.get(i)) {
                    isDuplicated = true
                }
            }
            if (isDuplicated) {
                isDuplicated = false
                continue
            } else {
                mAnswer.set(count, temp)
                count++
            }
            if (count > 2) {
                break
            }
        }
        mAnswer.set(3, mCurrentNumber)
        Collections.shuffle(mAnswer)
    }

    private fun setAnswerButtonText() {
        answer_one.setText(mQuestions[mAnswer.get(0)].answerData)
        answer_two.setText(mQuestions[mAnswer.get(1)].answerData)
        answer_three.setText(mQuestions[mAnswer.get(2)].answerData)
    }
}


