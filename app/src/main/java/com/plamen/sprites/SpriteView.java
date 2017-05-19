/**
 *
 */
package com.plamen.sprites;

import com.plamen.sprites.drawable.SpriteTile;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

public class SpriteView extends View {
    SpriteTile st;

    public SpriteView(Context context) {
        super(context);
        init(context);
    }

    private void init(final Context context) {
        st = new SpriteTile(R.drawable.out, R.xml.out, context, this);

        final View that = this;
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                st.setRunAnimation(true);
                that.invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        st.draw(canvas);
    }

}
