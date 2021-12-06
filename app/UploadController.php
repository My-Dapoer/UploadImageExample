<?php

namespace App\Http\Controllers\Api;

use App\Transaksi;
use App\TransaksiDetail;
use App\User;
use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use Illuminate\Support\Facades\Validator;

class UploadController extends Controller {

    public function upload(Request $request) {
        $fileName = '';
        if ($request->file->getClientOriginalName()) {
            $file = str_replace(' ', '', $request->file->getClientOriginalName());
            $fileName = date('mYdHs') . rand(1, 999) . '_' . $file;
            $request->file->storeAs('public/file', $fileName);
        } else {
            return $this->error('File file wajib');
        }

        return response()->json([
            'success' => 1,
            'message' => 'Berhasil',
            'data' => $fileName
        ]);
    }

    public function error($pasan) {
        return response()->json([
            'success' => 0,
            'message' => $pasan
        ]);
    }
}
