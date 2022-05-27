import tensorflow as tf
import os
from tensorflow.python.keras.layers import Layer
from tensorflow.python.keras import backend as K

class AttentionLayer(Layer,object):

    def __init__(self, **kwargs):
        super(AttentionLayer, self).__init__(**kwargs)

    def build(self, input_shape):

        msg = "Required input should be of type list"
        assert isinstance(input_shape, list), msg

        # Create a trainable weight variable for this layer.

        self.W_decoder = self.add_weight(name='W_decoder',
                                    shape=tf.TensorShape((input_shape[0][2], input_shape[0][2])),
                                    initializer='uniform',
                                    trainable=True)
        self.W_encoder = self.add_weight(name='W_encoder',
                                    shape=tf.TensorShape((input_shape[1][2], input_shape[0][2])),
                                    initializer='uniform',
                                    trainable=True)
        self.W_combined = self.add_weight(name='W_combined',
                                    shape=tf.TensorShape((input_shape[0][2], 1)),
                                    initializer='uniform',
                                    trainable=True)

        super(AttentionLayer, self).build(input_shape)  # Be sure to call this at the end



    def call(self, inputs):

        """ inputs = [encoder_output_sequence, decoder_output_sequence] """
        msg = "Required input should be of type list"
        assert isinstance(inputs, list), msg

        encoder_out_seq, decoder_out_seq = inputs

        """ Computing H_decoder.W_decoder where H_decoder are hidden states of decoder """

        # encoder_out_seq shape == (batch_size, max_length, hidden_size)
        
        # decoder_out_seq shape == (batch_size, hidden size)
        # decoder_out_seq_expand shape == (batch_size, 1, hidden size)
        # This needs to be done to perform addition to calculate the score
        
        decoder_out_seq_expand = tf.expand_dims(decoder_out_seq, 1)

        one = K.dot(decoder_out_seq_expand, self.W_decoder) # H_decoder.W_decoder

        two = K.dot(encoder_out_seq, self.W_encoder)	# H_encoder.W_encoder

        three = K.tanh (one + two)			# tanh(H_decoder.W_decoder + H_encoder.W_encoder)

        score = K.dot(three, W_combined)	# Final allignment score.

        attention_wts = K.softmax(score)

        return score,attention_wts

    def compute_output_shape(self, input_shape):
        """ Outputs produced by the layer """
        return tf.TensorShape((input_shape[1][0], input_shape[1][1], input_shape[1][2])),tf.TensorShape((input_shape[1][0], input_shape[1][1], input_shape[0][1]))
    