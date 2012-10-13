module Main where

import qualified Network (listenOn, withSocketsDo, accept, PortID(..), Socket)
import Network.Socket

import System.IO (hSetBuffering, hGetLine, hGetBuf, hPutStrLn, BufferMode(..), Handle, IOMode(WriteMode), withBinaryFile)
import Control.Concurrent (forkIO)
import Data.ByteString.Lazy (hGetContents, hPut)
import Data.Time.Clock
import Data.Time.Format
import System.Locale
import Control.Monad (liftM)

startTcpServer :: Int -> IO ()
startTcpServer port = withSocketsDo $ do
    sock <- Network.listenOn $ Network.PortNumber $ fromIntegral port
    putStrLn $ "Listening on " ++ (show port)
    sockHandler sock

sockHandler :: Socket -> IO ()
sockHandler sock = do
    putStrLn "start accept"
    (handle, _, _) <- Network.accept sock
    putStrLn "accept done"
    hSetBuffering handle NoBuffering
    forkIO $ commandProcessor handle
    sockHandler sock

dateTime2FileName :: UTCTime -> String
dateTime2FileName now = formatTime defaultTimeLocale "%y%m%d_%H%M%S.jpg" now

commandProcessor :: Handle -> IO ()
commandProcessor handle = do
    putStrLn "recive..."
    now <- getCurrentTime
    let filename = dateTime2FileName now
    -- getCurrentTime >>= (liftM dateTime2FileName) >>= \filename ->
    withBinaryFile filename WriteMode $ \f -> hGetContents handle >>= hPut f
    putStrLn "done"

main = startTcpServer 8412

