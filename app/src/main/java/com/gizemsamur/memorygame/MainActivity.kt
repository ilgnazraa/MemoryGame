package com.gizemsamur.memorygame

import android.animation.ArgbEvaluator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
//import android.support.design.widget.Snackbar
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.BoringLayout.make
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.gizemsamur.memorygame.models.BoardSize
import com.gizemsamur.memorygame.models.MemoryCard
import com.gizemsamur.memorygame.models.MemoryGame
import com.gizemsamur.memorygame.utils.DEFAULT_ICONS

class MainActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "MainActivity"
    }

    private lateinit var clRoot: ConstraintLayout
    private lateinit var rvBoard: RecyclerView
    private lateinit var tvHamle: TextView
    private lateinit var tvEslesme: TextView


    private lateinit var memoryGame: MemoryGame
    private lateinit var adapter: MemoryBoardAdapter
    private var boardSize:BoardSize=BoardSize.EASY


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clRoot=findViewById(R.id.clRoot)
        rvBoard=findViewById(R.id.rvBoard)
        tvHamle=findViewById(R.id.tvHamle)
        tvEslesme=findViewById(R.id.tvEslesme)

        setupBoard()

        /*tvEslesme.setTextColor(ContextCompat.getColor(this,R.color.color_progress_none))
        memoryGame = MemoryGame(boardSize)
        //println("board size:" + boardSize.numCards)
        //println("toplam size:" + randomizedImages.size)
        adapter = MemoryBoardAdapter(this,boardSize, memoryGame.cards, object: MemoryBoardAdapter.CardClickListener{
            override fun onCardClicked(position: Int) {
                updateGameWithFlip(position)
            }
        })
        rvBoard.adapter = adapter
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager=GridLayoutManager(this,boardSize.getWidth())*/

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.mi_refresh->{
                if(memoryGame.getNumMoves()>0 && !memoryGame.haveWonGames()){
                    showAlertDialog("Oyundan çıkmak istiyor musunuz?",null,View.OnClickListener {
                        setupBoard()
                    })

                }else{
                    setupBoard()
                }
                return true

            }
            R.id.mi_new_size->{
                showNewSizeDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showNewSizeDialog() {
        val boardSizeView=LayoutInflater.from(this).inflate(R.layout.dialog_board_size,null)
        val radioGroupSize=boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        when(boardSize){
            BoardSize.EASY-> radioGroupSize.check(R.id.rbEasy)
            BoardSize.HARD-> radioGroupSize.check(R.id.rbHard)
        }
        showAlertDialog("Choose new size",boardSizeView,View.OnClickListener {
            boardSize=when(radioGroupSize.checkedRadioButtonId){
                R.id.rbEasy->BoardSize.EASY
                else->BoardSize.HARD
            }
            setupBoard()
        })
    }

    private fun showAlertDialog(title:String,view: View?,positiveClickListener:View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Hayır",null)
            .setPositiveButton("Evet"){_,_->
                positiveClickListener.onClick(null)

            }.show()


    }

    private fun setupBoard() {
        when(boardSize){
            BoardSize.EASY -> {
                tvHamle.text="Kolay"
                tvEslesme.text="Eşleşme:0/16"
            }
            BoardSize.HARD -> {
                tvHamle.text="Zor"
                tvEslesme.text="Eşleşme:0/36"
            }
        }
        tvEslesme.setTextColor(ContextCompat.getColor(this,R.color.color_progress_none))
        memoryGame = MemoryGame(boardSize)
        //println("board size:" + boardSize.numCards)
        //println("toplam size:" + randomizedImages.size)
        adapter = MemoryBoardAdapter(this,boardSize, memoryGame.cards, object: MemoryBoardAdapter.CardClickListener{
            override fun onCardClicked(position: Int) {
                updateGameWithFlip(position)
            }
        })
        rvBoard.adapter = adapter
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager=GridLayoutManager(this,boardSize.getWidth())
    }
    private fun updateGameWithFlip(position: Int) {
        //error checking
        if(memoryGame.haveWonGames()){
            //Alert the user of an invalid move
            Toast.makeText(this, "Kazandınız!", Toast.LENGTH_LONG).show()
            return
        }
        if(memoryGame.isCardFaceUp(position)){
            //Alert the user of an invalid move
            Toast.makeText(this, "Geçersiz Hamle!", Toast.LENGTH_SHORT).show()
            return
        }
        //Actually flip over the card
        if(memoryGame.flipCard(position)){
            Log.i(TAG,"Eşleşme bulundu.Bulunan eşleşmeler:${memoryGame.numPairsFound}")
            val color= ArgbEvaluator().evaluate(
                memoryGame.numPairsFound.toFloat()/boardSize.getNumPairs(),
                ContextCompat.getColor(this,R.color.color_progress_none),
                ContextCompat.getColor(this,R.color.color_progress_full)
            )as Int
            tvEslesme.setTextColor(color)
            tvEslesme.text="Eşleşme:${memoryGame.numPairsFound}/${boardSize.getNumPairs()}"
            if(memoryGame.haveWonGames()){
                Toast.makeText(this,"Kazandınız!",Toast.LENGTH_LONG).show()
            }
        }
        tvHamle.text="Hamle:${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()

    }
}