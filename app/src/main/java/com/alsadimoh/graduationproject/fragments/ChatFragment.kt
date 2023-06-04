package com.alsadimoh.graduationproject.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.alsadimoh.graduationproject.classes.CommonConstants
import com.alsadimoh.graduationproject.classes.CommonConstants.myPic
import com.alsadimoh.graduationproject.R
import com.alsadimoh.graduationproject.chatUtils.adapter.ImageMessagesAdapter
import com.alsadimoh.graduationproject.chatUtils.adapter.MessagesAdapter
import com.alsadimoh.graduationproject.chatUtils.models.ChattedUsers
import com.alsadimoh.graduationproject.chatUtils.models.MessageModel
import com.alsadimoh.graduationproject.databinding.FragmentChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.util.*

class ChatFragment : Fragment(R.layout.fragment_chat) {

    lateinit var binding : FragmentChatBinding
    lateinit var adapter: MessagesAdapter
    lateinit var data: MutableList<MessageModel>
    private val args:ChatFragmentArgs by navArgs()
    private lateinit var mDbRef: DatabaseReference
    private lateinit var receiverUid: String
    private lateinit var senderUid: String
    private var senderProfileStringImage = myPic
    private lateinit var db: FirebaseFirestore

    private var senderRoom: String? = null
    private var receiverRoom: String? = null

    private var isImage = false
    private lateinit var images:MutableList<String>
    lateinit var imagesAdapter: ImageMessagesAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false)

        CommonConstants.showProgressDialog(requireActivity())

        val myShared = requireActivity().getSharedPreferences(CommonConstants.mySharedPrefKey, Context.MODE_PRIVATE)
        val senderProfileImageUrl = myShared.getString(CommonConstants.userImage, null)

        if (senderProfileImageUrl!= null){
            Picasso.get().load(senderProfileImageUrl).into(object : com.squareup.picasso.Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    senderProfileStringImage = CommonConstants.bitmapToString(bitmap!!)
                }
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
            })
        }


        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    if (result.data != null) {
                        isImage = true
                        binding.edMessage.text.clear()
                        binding.edMessage.visibility = View.GONE
                        binding.rvChosenImages.visibility = View.VISIBLE
                        binding.linearlike.visibility = View.GONE
                        binding.linearSend.visibility = View.VISIBLE
                       if (result.data!!.data != null){
                           val uri = result.data!!.data.toString()
                           Picasso.get().load(uri).into(object : com.squareup.picasso.Target {
                               override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                                   imagesAdapter.data.add(CommonConstants.bitmapToString(bitmap!!))
                                   imagesAdapter.notifyDataSetChanged()
                               }
                               override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                               override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
                           })
                       }else{
                           imagesAdapter.data.add(CommonConstants.bitmapToString(result.data!!.extras?.get("data") as Bitmap))
                           imagesAdapter.notifyDataSetChanged()
                       }

                    }
                }
            }

        receiverUid = args.receiverId
        mDbRef = FirebaseDatabase.getInstance().reference
        db = Firebase.firestore
        senderUid = FirebaseAuth.getInstance().currentUser?.uid!!

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        data = mutableListOf()
        adapter = MessagesAdapter(requireActivity(), data)
        binding.rvChat.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(requireActivity())
        binding.rvChat.layoutManager = linearLayoutManager

        images= mutableListOf()
        imagesAdapter = ImageMessagesAdapter(requireActivity(),images)
        binding.rvChosenImages.adapter = imagesAdapter
        binding.rvChosenImages.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)

        imagesAdapter.onClickDeleteItem = {image ->
            imagesAdapter.data.remove(image)
            adapter.notifyDataSetChanged()
            if ( imagesAdapter.data.isEmpty()){
                binding.edMessage.visibility = View.VISIBLE
                binding.rvChosenImages.visibility = View.GONE
                binding.linearlike.visibility = View.VISIBLE
                binding.linearSend.visibility = View.GONE
                isImage = false
            }
        }

        binding.btnChooseFromStudio.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1000
                )
            } else {

                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"

                resultLauncher.launch(Intent.createChooser(intent, "Select Picture"))
            }
        }

        binding.btnChooseFromCamera.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    1001
                )
            } else {

                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                resultLauncher.launch(cameraIntent)
            }
        }

        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    CommonConstants.hideProgressDialog()
                    data.clear()
                    for (i in snapshot.children) {
                        val messageModel = i.getValue(MessageModel::class.java)
                        data.add(messageModel!!)
                    }
                    adapter.notifyDataSetChanged()
                    linearLayoutManager.scrollToPositionWithOffset(data.size-1,10)
                }

                override fun onCancelled(error: DatabaseError) {
                    CommonConstants.hideProgressDialog()
                }
            })

        binding.edMessage.addTextChangedListener {
            if (binding.edMessage.text.isEmpty()) {
                binding.linearlike.visibility = View.VISIBLE
                binding.linearSend.visibility = View.GONE
            } else {
                binding.linearlike.visibility = View.GONE
                binding.linearSend.visibility = View.VISIBLE
            }
        }

        binding.btnSend.setOnClickListener {
           if (isImage){
               for (i in imagesAdapter.data){
                   sendText(i, isImage)
               }
               imagesAdapter.data.clear()
               imagesAdapter.notifyDataSetChanged()

               if ( imagesAdapter.data.isEmpty()){
                   binding.edMessage.visibility = View.VISIBLE
                   binding.rvChosenImages.visibility = View.GONE
                   binding.linearlike.visibility = View.VISIBLE
                   binding.linearSend.visibility = View.GONE
                   isImage = false
               }

           }else{
               if (binding.edMessage.text.isNotEmpty()) {
                   sendText(binding.edMessage.text.toString(), isImage)
               }
           }
        }

        binding.btnLike.setOnClickListener {
            sendText("👍",isImage)
        }

        return binding.root
    }

    private fun sendText(text: String,isImage:Boolean) {
        val message = MessageModel(
            senderProfileStringImage,
            text,
            senderUid,
            System.currentTimeMillis().toString(),
            Date().toString(),
            isImage
        )

        mDbRef.child("chats").child(senderRoom!!).child("messages").push()
            .setValue(message).addOnSuccessListener {
                mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                    .setValue(message).addOnSuccessListener {

                        db.collection("chattedUsers").whereEqualTo("roomId", senderRoom).get()
                            .addOnSuccessListener { querySnapshot ->
                                val senderChattedUsers = ChattedUsers(
                                    null,
                                    senderUid,
                                    receiverUid,
                                    if (isImage){"تم ارسال صورة"}else{text},
                                    System.currentTimeMillis().toString(),
                                    senderRoom
                                )

                                db.collection("chattedUsers").whereEqualTo("roomId", receiverRoom).get()
                                    .addOnSuccessListener { qSnapshot ->

                                        val receiverChattedUsers = ChattedUsers(
                                            null,
                                            receiverUid,
                                            senderUid,
                                            if (isImage){"تم ارسال صورة"}else{text},
                                            System.currentTimeMillis().toString(),
                                            receiverRoom
                                        )

                                        if (querySnapshot.isEmpty) {
                                            db.collection("chattedUsers").add(senderChattedUsers)
                                            db.collection("chattedUsers").add(receiverChattedUsers).addOnSuccessListener {
                                                for (i in adapter.data.indices){
                                                   if (adapter.data[i].message == text ) {
                                                       adapter.data[i].isSentSuccessfully = true
                                                       adapter.notifyItemChanged(i)
                                                   }
                                                }
                                            }
                                        } else {
                                            // ما رضي ياخد مودل
                                            senderChattedUsers.id = querySnapshot.first().id
                                            receiverChattedUsers.id =  qSnapshot.first().id
                                            updateDoc(senderChattedUsers,text)
                                            updateDoc(receiverChattedUsers,text)
                                        }
                                    }
                            }
                    }

            }
        binding.edMessage.text.clear()
    }

    private fun updateDoc(i: ChattedUsers,text: String) {
        val document = HashMap<String, Any>()
        document["userId"] = i.userId!!
        document["lastMessageBody"] = i.lastMessageBody!!
        document["lastMessageTime"] = i.lastMessageTime!!
        document["receiverId"] = i.receiverId!!
        document["roomId"] = i.roomId!!

        /////  ///
        db.collection("chattedUsers").document(i.id!!)
            .update(document).addOnSuccessListener {
                if (i.userId == receiverUid){
                    for (index in adapter.data.indices){
                        if (adapter.data[index].message == text ) {
                            adapter.data[index].isSentSuccessfully = true
                            adapter.notifyItemChanged(index)
                        }
                    }
                }
            }
    }

}