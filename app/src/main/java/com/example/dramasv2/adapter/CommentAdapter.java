package com.example.dramasv2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dramasv2.R;
import com.example.dramasv2.modelo.Comment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private Context context;
    private List<Comment> commentList;
    private OnCommentClickListener onCommentClickListener;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    public void setOnCommentClickListener(OnCommentClickListener listener) {
        this.onCommentClickListener = listener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.textViewUser.setText(comment.getUser());
        holder.textViewComment.setText(comment.getText());
        holder.textViewTime.setText(comment.getTime());

        holder.textViewDelete.setOnClickListener(v -> deleteComment(comment));

        holder.textViewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCommentClickListener != null) {
                    onCommentClickListener.onCommentClick(comment);
                }
            }
        });
    }

    private void deleteComment(Comment comment) {
        // Eliminar el comentario localmente
        int index = commentList.indexOf(comment);
        if (index != -1) {
            commentList.remove(index);
            notifyItemRemoved(index);
        }

        // Eliminar el comentario de Firebase Realtime Database
        DatabaseReference commentsReference = FirebaseDatabase.getInstance().getReference("comments");
        Query query = commentsReference.orderByChild("user").equalTo(comment.getUser());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment dbComment = snapshot.getValue(Comment.class);
                    if (dbComment != null && dbComment.getUser().equals(comment.getUser())) {
                        snapshot.getRef().removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    // Comentario eliminado exitosamente de la base de datos
                                })
                                .addOnFailureListener(e -> {
                                    // Error al eliminar el comentario de la base de datos
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar error
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUser;
        TextView textViewComment;
        TextView textViewTime;
        TextView textViewDelete;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUser = itemView.findViewById(R.id.textViewUser);
            textViewComment = itemView.findViewById(R.id.textViewComment);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewDelete = itemView.findViewById(R.id.textViewDelete);
        }
    }

    public interface OnCommentClickListener {
        void onCommentClick(Comment comment);
    }
}
